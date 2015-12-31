package io.otrl.library.rest.hooks

import spray.httpx.marshalling.{ToResponseMarshallable => Response}

/**
  * Hooks for REST functions for use in adding functionality around said functions.
  */
trait RestHooks[T] {

  protected def postHook(postFunction: => Response): Response

  protected def getHook(getFunction: => Response): Response

  protected def putHook(putFunction: => Response): Response

  protected def deleteHook(deleteFunction: => Response): Response

}
