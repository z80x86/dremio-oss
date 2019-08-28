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
package com.dremio.jdbc;

import static java.sql.Connection.TRANSACTION_NONE;
import static java.sql.Connection.TRANSACTION_READ_COMMITTED;
import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;
import static java.sql.Connection.TRANSACTION_REPEATABLE_READ;
import static java.sql.Connection.TRANSACTION_SERIALIZABLE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Savepoint;

import org.junit.Test;


/**
 * Test for Dremio's implementation of Connection's main transaction-related
 * methods.
 */
public class ConnectionTransactionMethodsTest extends JdbcWithServerTestBase {

  ////////////////////////////////////////
  // Transaction mode methods:

  //////////
  // Transaction isolation level:

  @Test
  public void testGetTransactionIsolationSaysNone() throws SQLException {
    assertThat( getConnection().getTransactionIsolation(), equalTo( TRANSACTION_NONE ) );
  }

  @Test
  public void testSetTransactionIsolationNoneExitsNormally() throws SQLException {
    getConnection().setTransactionIsolation( TRANSACTION_NONE );
  }

  // Test trying to set to unsupported isolation levels:

  // (Sample message:  "Can't change transaction isolation level to
  // Connection.TRANSACTION_REPEATABLE_READ (from Connection.TRANSACTION_NONE).
  // (Dremio is not transactional.)" (as of 2015-04-22))

  @Test( expected = SQLFeatureNotSupportedException.class )
  public void testSetTransactionIsolationReadUncommittedThrows() throws SQLException {
    try {
      getConnection().setTransactionIsolation( TRANSACTION_READ_UNCOMMITTED );
    }
    catch ( SQLFeatureNotSupportedException e ) {
      // Check a few things in an error message:
      assertThat( "Missing requested-level string",
                  e.getMessage(), containsString( "TRANSACTION_READ_UNCOMMITTED" ) );
      assertThat( "Missing (or reworded) expected description",
                  e.getMessage(), containsString( "transaction isolation level" ) );
      assertThat( "Missing current-level string",
                  e.getMessage(), containsString( "TRANSACTION_NONE" ) );
      throw e;
    }
  }

  @Test( expected = SQLFeatureNotSupportedException.class )
  public void testSetTransactionIsolationReadCommittedThrows() throws SQLException {
    getConnection().setTransactionIsolation( TRANSACTION_READ_COMMITTED );
  }
  @Test( expected = SQLFeatureNotSupportedException.class )
  public void testSetTransactionIsolationRepeatableReadThrows() throws SQLException {
    getConnection().setTransactionIsolation( TRANSACTION_REPEATABLE_READ );
  }
  @Test( expected = SQLFeatureNotSupportedException.class )
  public void testSetTransactionIsolationSerializableThrows() throws SQLException {
    getConnection().setTransactionIsolation( TRANSACTION_SERIALIZABLE );
  }

  @Test( expected = JdbcApiSqlException.class )
  public void testSetTransactionIsolationBadIntegerThrows() throws SQLException {
    getConnection().setTransactionIsolation( 15 );  // not any TRANSACTION_* value
  }


  //////////
  // Auto-commit mode.

  @Test
  public void testGetAutoCommitSaysAuto() throws SQLException {
    // Auto-commit should always be true.
    assertThat( getConnection().getAutoCommit(), equalTo( true ) );
  }

  @Test
  public void testSetAutoCommitTrueExitsNormally() throws SQLException {
    // Setting auto-commit true (redundantly) shouldn't throw exception.
    getConnection().setAutoCommit( true );
  }


  ////////////////////////////////////////
  // Transaction operation methods:

  @Test( expected = JdbcApiSqlException.class )
  public void testCommitThrows() throws SQLException {
    // Should fail saying because in auto-commit mode (or maybe because not
    // supported).
    getConnection().commit();
  }

  @Test( expected = JdbcApiSqlException.class )
  public void testRollbackThrows() throws SQLException {
    // Should fail saying because in auto-commit mode (or maybe because not
    // supported).
    getConnection().rollback();
  }


  ////////////////////////////////////////
  // Savepoint methods:

  @Test( expected = SQLFeatureNotSupportedException.class )
  public void testSetSavepointUnamed() throws SQLException {
    getConnection().setSavepoint();
  }

  @Test( expected = SQLFeatureNotSupportedException.class )
  public void testSetSavepointNamed() throws SQLException {
    getConnection().setSavepoint( "savepoint name" );
  }

  @Test( expected = SQLFeatureNotSupportedException.class )
  public void testRollbackSavepoint() throws SQLException {
    getConnection().rollback( (Savepoint) null );
  }

  @Test( expected = SQLFeatureNotSupportedException.class )
  public void testReleaseSavepoint() throws SQLException {
    getConnection().releaseSavepoint( (Savepoint) null );
  }

}
