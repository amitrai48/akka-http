/*
 * Copyright (C) 2009-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package akka.http.testkit

import akka.http.model.HttpEntity
import akka.http.unmarshalling.FromEntityUnmarshaller
import akka.stream.FlowMaterializer
import org.scalatest.Suite
import org.scalatest.matchers.Matcher

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future, Await }
import scala.util.Try

trait ScalatestUtils extends MarshallingTestUtils {
  import org.scalatest.Matchers._
  def evaluateTo[T](value: T): Matcher[Future[T]] =
    equal(value).matcher[T] compose (x ⇒ Await.result(x, 1.second))

  def haveFailedWith(t: Throwable): Matcher[Future[_]] =
    equal(t).matcher[Throwable] compose (x ⇒ Await.result(x.failed, 1.second))

  def unmarshalToValue[T: FromEntityUnmarshaller](value: T)(implicit ec: ExecutionContext, mat: FlowMaterializer): Matcher[HttpEntity] =
    equal(value).matcher[T] compose (unmarshalValue(_))

  def unmarshalTo[T: FromEntityUnmarshaller](value: Try[T])(implicit ec: ExecutionContext, mat: FlowMaterializer): Matcher[HttpEntity] =
    equal(value).matcher[Try[T]] compose (unmarshal(_))
}

trait ScalatestRouteTest extends RouteTest with TestFrameworkInterface.Scalatest with ScalatestUtils { this: Suite ⇒ }