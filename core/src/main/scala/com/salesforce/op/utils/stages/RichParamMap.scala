/*
 * Copyright (c) 2017, Salesforce.com, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.op.utils.stages

import com.salesforce.op.features.TransientFeature
import org.apache.spark.ml.PipelineStage
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.sql.types.StructType

object RichParamMap {

  /**
   * Enrichment functions for ParamMap
   *
   * @param params Metadata
   */
  implicit class RichParamMap(val params: ParamMap) extends AnyVal {

    /**
     * Extract param names and values from param map
     * @return map of names to values
     */
    def getAsMap(): Map[String, Any] =
      params.toSeq.map(pp => pp.param.name -> pp.value).toMap.map{
        case (k, v: Array[_]) =>
          if (v.headOption.exists(_.isInstanceOf[TransientFeature])) {
            k -> v.map(_.asInstanceOf[TransientFeature].toJsonString())
          } else k -> v
        case (k, v: StructType) => k -> v.toString()
        case (k, v: PipelineStage) => k -> v.getClass.getName
        case (k, v: Option[_]) =>
          if (v.exists(_.isInstanceOf[PipelineStage])) {
            k -> v.getClass.getName
          } else k -> v
        case (k, v) => k -> v
      }

  }

}
