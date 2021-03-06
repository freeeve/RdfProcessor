package com.elegantcoding.rdfprocessor.rdftriple

package object types {
  type OptionString = Option[String]
  type RdfTriple = RdfTripleTrait[_,_,_]
}

import types._

trait RdfTripleTrait[ST,PT,OT] {
  val subject: ST
  val predicate: PT
  val obj: OT

  def isValid(): Boolean

  def size(): Int

  def subjectString: String

  def predicateString: String

  def objectString: String
}

abstract class AbstractRdfTriple[ST,PT,OT](val subject: ST, val predicate: PT, val obj: OT) extends RdfTripleTrait[ST,PT,OT] {}

case class ValidRdfTriple(override val subject: String, override val predicate: String, override val obj: String)
  extends AbstractRdfTriple[String,String,String](subject, predicate, obj) {

  def isValid() = true

  def size() = 3

  def subjectString = subject

  def predicateString = predicate

  def objectString = obj
}

abstract class BaseInvalidRdfTriple(subject: OptionString, predicate: OptionString, obj: OptionString)
  extends AbstractRdfTriple[OptionString,OptionString,OptionString](subject, predicate, obj) {

  private val size_ = (subject, predicate, obj) match {
    case (None, None, None) => 0
    case (None, None, Some(c)) => 1
    case (None, Some(b), Some(c)) => 2
    case _ => 3
  }

  private def getOptionStringValue(optionString: OptionString): String = {
    optionString match {
      case Some(string) => string
      case _ => ""
    }
  }

  def isValid = false

  def size = size_

  def subjectString = getOptionStringValue(subject)

  def predicateString = getOptionStringValue(predicate)

  def objectString = getOptionStringValue(obj)
}

abstract class InvalidRdfTripleReason(errorString: String)

case class InvalidRdfTripleUnknown(errorString: String) extends InvalidRdfTripleReason(errorString)

case class InvalidRdfTripleSize0(errorString: String) extends InvalidRdfTripleReason(errorString)

case class InvalidRdfTripleSize1(errorString: String) extends InvalidRdfTripleReason(errorString)

case class InvalidRdfTripleSize2(errorString: String) extends InvalidRdfTripleReason(errorString)

case class NonEnglishInvalidRdfTriple(errorString: String) extends InvalidRdfTripleReason(errorString)

case class InvalidRdfTriple(override val subject: OptionString, override val predicate: OptionString, override val obj: OptionString, invalidRdfTripleReason: InvalidRdfTripleReason)
  extends BaseInvalidRdfTriple(subject, predicate, obj) {
}

object InvalidRdfTriple {
  def apply() = new InvalidRdfTriple(None, None, None, new InvalidRdfTripleSize0(""))

  def apply(subject: String) = new InvalidRdfTriple(Some(subject), None, None, new InvalidRdfTripleSize1(""))

  def apply(subject: String, predicate: String) = new InvalidRdfTriple(Some(subject), Some(predicate), None, new InvalidRdfTripleSize2(""))

  def apply(subject: String, predicate: String, obj: String) = new InvalidRdfTriple(Some(subject), Some(predicate), Some(obj), InvalidRdfTripleUnknown(""))
}
