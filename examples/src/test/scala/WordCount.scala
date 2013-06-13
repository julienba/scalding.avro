package scalding.avro.examples

import org.specs.Specification
import scalding.avro.TypedUnpackedAvroSource
import org.apache.avro.Schema
import com.twitter.scalding._

class WordCountTest extends Specification with TupleConversions {

  val jsonSchema = io.Source.fromURL(getClass.getResource("/wc.avsc")).mkString

  "A WordCount job" should {   
    JobTest("scalding.avro.examples.WordCountAvroJob")
      .arg("input", "inputFile")
      .arg("output", "outputFile")
      .source(TextLine("inputFile"), List((0, "hack hack hack and hack")))
      .sink[(String,Int)](TypedUnpackedAvroSource[(String,Long)]( "outputFile", Some(new Schema.Parser().parse(jsonSchema) ) )){ outputBuffer =>
        val outMap = outputBuffer.toMap
        "count words correctly" in {
          outMap("hack") must be_==(4)
          outMap("and") must be_==(1)
        }
      }
      .run
      .finish
  }
} 
