package sttp.tapir.model

import sttp.model.{Header, Headers, ResponseMetadata, StatusCode}
import sttp.tapir.server.ValuedEndpointOutput

import scala.collection.immutable.Seq

/** @param source
  *   The output, from which this response has been created. Might be [[ValuedEndpointOutput.Empty]] if no output is available.
  */
case class ServerResponse[+B](code: StatusCode, headers: Seq[Header], body: Option[B], source: Option[ValuedEndpointOutput[_]])
    extends ResponseMetadata {
  override def statusText: String = ""
  override def toString: String = s"ServerResponse($code,${Headers.toStringSafe(headers)})"

  def showShort: String = code.toString()
  def showCodeAndHeaders: String = s"$code (${Headers.toStringSafe(headers)})"
}

object ServerResponse {
  def notFound[B]: ServerResponse[B] = ServerResponse[B](StatusCode.NotFound, Nil, None, None)
}
