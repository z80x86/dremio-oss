/*
 * Copyright (C) 2017-2019 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio.sabot.op.copier;

import static com.dremio.common.util.MajorTypeHelper.getMajorTypeForField;

import javax.inject.Named;

import org.apache.arrow.memory.OutOfMemoryException;
import org.apache.arrow.vector.AllocationHelper;
import org.apache.arrow.vector.DensityAwareVector;
import org.apache.arrow.vector.ValueVector;

import com.dremio.common.AutoCloseables;
import com.dremio.common.types.TypeProtos.MajorType;
import com.dremio.common.types.Types;
import com.dremio.exec.exception.SchemaChangeException;
import com.dremio.exec.record.VectorAccessible;
import com.dremio.exec.record.VectorWrapper;
import com.dremio.exec.record.selection.SelectionVector2;
import com.dremio.sabot.exec.context.FunctionContext;


public abstract class CopierTemplate2 implements Copier {
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CopierTemplate2.class);

  private SelectionVector2 sv2;
  private VectorAccessible outgoing;

  /**
   * Use this flag to control when to set new capacity and density in outgoing vectors. If we are successful in
   * allocating the vectors with given capacity and density, we continue to use the same capacity and density with
   * allocateNew() to allocate memory. If we fail in allocate, we start from initial density and given capacity.
   */
  private boolean lastAllocationSucceeded;

  @Override
  public void setupRemover(FunctionContext context, VectorAccessible incoming, VectorAccessible outgoing) throws SchemaChangeException{
    this.sv2 = incoming.getSelectionVector2();
    this.outgoing = outgoing;
    doSetup(context, incoming, outgoing);
  }

  @Override
  public int copyRecords(int index, int recordCount){
    logger.debug("Copier4: Position to copy from {} records to copy {}", index, recordCount);
    int outgoingPosition = 0;
    double density = 0.01;
    boolean memoryAllocated = false;
    try {
      while (!memoryAllocated) {
        try {
          for (VectorWrapper<?> out : outgoing) {
            final ValueVector v = out.getValueVector();
            MajorType type = getMajorTypeForField(out.getField());
            if (!lastAllocationSucceeded) {
              if (v instanceof DensityAwareVector) {
                ((DensityAwareVector) v).setInitialCapacity(recordCount, density);
              } else {
                v.setInitialCapacity(recordCount);
              }

              logger.debug("Copier4: setting initial capacity for {} allocating memory for vector {} MajorType {}", recordCount, v.getClass(), type);
            }
            if (!Types.isFixedWidthType(type)) {
              /* VARCHAR, VARBINARY, UNION */
              out.getValueVector().allocateNew();
            } else {
              /* fixed width, list etc */
              AllocationHelper.allocate(out.getValueVector(), recordCount, 1);
            }
          }
          lastAllocationSucceeded = true;
          memoryAllocated = true;
        } catch (OutOfMemoryException ex) {
          logger.debug("Copier2: Failed to allocate memory for outgoing batch, retrying with reduced capacity");
          recordCount = recordCount/2;
          if (recordCount < 1) {
            logger.debug("Copier2: Unable to allocate memory for even 1 record");
            throw ex;
          }
          lastAllocationSucceeded = false;
          clearVectors();
        }
      }

      logger.debug("Copier2: allocated memory for all vectors in outgoing.");

      for(int svIndex = index; svIndex < index + recordCount; svIndex++, outgoingPosition++){
        doEval(sv2.getIndex(svIndex), outgoingPosition);
      }
    }catch(OutOfMemoryException ex){
      if(outgoingPosition == 0) {
        logger.debug("Copier2: Ran out of space in copy without copying a single record");
        throw ex;
      }
      logger.debug("Copier2: Ran out of space in copy, returning early");
    }

    return outgoingPosition;
  }
  private void clearVectors() {
    for (VectorWrapper<?> vw : outgoing) {
      final ValueVector v = vw.getValueVector();
      v.clear();
    }
  }

  @Override
  public void close() throws Exception {
    AutoCloseables.close(sv2);
  }

  public abstract void doSetup(@Named("context") FunctionContext context, @Named("incoming") VectorAccessible incoming, @Named("outgoing") VectorAccessible outgoing);
  public abstract void doEval(@Named("inIndex") int inIndex, @Named("outIndex") int outIndex);



}
