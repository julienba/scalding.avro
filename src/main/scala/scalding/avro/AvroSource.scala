/*  Copyright 2012 eBay, inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package scalding.avro

import cascading.avro.{PackedAvroScheme, AvroScheme}
import cascading.flow.FlowDef
import cascading.tuple.Fields
import cascading.scheme.Scheme
import com.twitter.scalding._
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificRecord

import org.apache.hadoop.mapred.{ RecordReader, OutputCollector, JobConf }

import cascading.scheme.local.TextDelimited

trait UnpackedAvroFileScheme extends Source {
  val schema: Option[Schema]

  override def hdfsScheme: Scheme[JobConf,RecordReader[_,_],OutputCollector[_,_],_,_] = 
    HadoopSchemeInstance(new AvroScheme(schema.getOrElse(null))).asInstanceOf[Scheme[org.apache.hadoop.mapred.JobConf,org.apache.hadoop.mapred.RecordReader[_,_],org.apache.hadoop.mapred.OutputCollector[_,_],_,_]] 

  override val localScheme = new TextDelimited(Fields.ALL)

}

trait PackedAvroFileScheme[AvroType] extends Mappable[AvroType] {
  val schema : Schema

  override def hdfsScheme: Scheme[JobConf,RecordReader[_,_],OutputCollector[_,_],_,_] = 
    HadoopSchemeInstance(new PackedAvroScheme[AvroType](schema)).asInstanceOf[Scheme[org.apache.hadoop.mapred.JobConf,org.apache.hadoop.mapred.RecordReader[_,_],org.apache.hadoop.mapred.OutputCollector[_,_],_, _]]
}

object TypedUnpackedAvroSource {
  def apply[TupleType : Manifest : TupleConverter](path: String, schema: Option[Schema]) = 
  new TypedUnpackedAvroSource[TupleType](Seq(path), schema)  
}

case class TypedUnpackedAvroSource[TupleType](paths: Seq[String], override val schema: Option[Schema])
(implicit val mf : Manifest[TupleType], override val converter: TupleConverter[TupleType]) 
extends FixedPathSource(paths: _*)
with UnpackedAvroFileScheme with Mappable[TupleType]



object UnpackedAvroSource {
  def apply(path: String, schema: Option[Schema]) = new UnpackedAvroSource(Seq(path), schema)
}

case class UnpackedAvroSource(p: Seq[String], override val schema: Option[Schema] = None)
extends FixedPathSource(p: _*) with UnpackedAvroFileScheme

object PackedAvroSource {
  def apply[AvroType : AvroSchemaType : Manifest : TupleConverter](path: String)
  = new PackedAvroSource[AvroType](Seq(path))
  def apply[AvroType : AvroSchemaType : Manifest : TupleConverter](paths: Seq[String])
  = new PackedAvroSource[AvroType](paths)
}

case class PackedAvroSource[AvroType : Manifest: AvroSchemaType : TupleConverter](paths: Seq[String])
extends FixedPathSource(paths: _*) with PackedAvroFileScheme[AvroType]  {
   val schemaType = implicitly[AvroSchemaType[AvroType]]
   override val schema = schemaType.schema
   override val converter = implicitly[TupleConverter[AvroType]]
} 





