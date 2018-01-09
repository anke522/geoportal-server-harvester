/* 
 * Copyright 2016 Esri, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

define(["dojo/_base/declare",
        "dojo/_base/lang",
        "dojox/encoding/digests/SHA256"
      ],
  function(declare,lang,dig){
  
    return {
      encode: function(text) {
        text = encodeURIComponent(text);
        var crc = this._hash(text);
        var encoded = btoa(text);
        return crc + encoded;
      },
      
      decode: function(text) {
        if (text.length<44) {
          return text;
        }
        var crc = text.substr(0,44);
        var encoded = text.slice(44);
        
        var decoded = atob(encoded);
        if (this._hash(decoded)!=crc) {
          return text;
        }
        
        return decodeURIComponent(decoded);
      },

      _hash: function (string) {
        return dig(string);
      }
    };
});



