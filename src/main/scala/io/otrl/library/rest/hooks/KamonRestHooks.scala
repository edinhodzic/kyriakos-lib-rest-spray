package io.otrl.library.rest.hooks

import com.typesafe.scalalogging.LazyLogging
import io.otrl.library.utils.ManifestUtils
import kamon.Kamon
import kamon.trace.TraceContext
import spray.httpx.marshalling.{ToResponseMarshallable => Response}

import scala.language.postfixOps

/**
  * Kamon REST hooks with Kamon trace instrumentation around REST functions.
  * @tparam T
  */
trait KamonRestHooks[T] extends RestHooks[T] with LazyLogging {

  // TODO no implicit manifest?
  private val domain: String = ManifestUtils.simpleName(manifest)

  override protected def postHook(postFunction: => Response): Response =
    kamonTrace(s"$domain-post-trace") {
      postFunction
    }

  override protected def getHook(getFunction: => Response): Response =
    kamonTrace(s"$domain-get-trace") {
      getFunction
    }

  override protected def putHook(putFunction: => Response): Response =
    kamonTrace(s"$domain-put-trace") {
      putFunction
    }

  override protected def deleteHook(deleteFunction: => Response): Response =
    kamonTrace(s"$domain-delete-trace") {
      deleteFunction
    }

  private def kamonTrace(name: String)(function: => Response): Response = {
    logger debug s"starting kamon trace [$name]"
    val traceContext: TraceContext = Kamon.tracer.newContext(name)
    val response: Response = function
    traceContext finish()
    response
  }

}
