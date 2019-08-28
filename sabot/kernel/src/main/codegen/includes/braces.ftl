<#--

    Copyright (C) 2017-2019 Dremio Corporation

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<#-- Copyright 2016 Dremio Corporation -->
     | < L_CONTAINS: "CONTAINS(" > { pushState(); }: LUCENE_DEFAULT
     | < LBRACE_D: "{" (" ")* ["d","D"] >
     | < LBRACE_T: "{" (" ")* ["t","T"] >
     | < LBRACE_TS: "{" (" ")* ["t","T"] ["s","S"] >
     | < LBRACE_FN: "{" (" ")* ["f","F"] ["n","N"] >
     | < LBRACE: "{" >
     | < RBRACE: "}" >
