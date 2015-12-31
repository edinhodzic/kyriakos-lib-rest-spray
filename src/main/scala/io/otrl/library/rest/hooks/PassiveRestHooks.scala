package io.otrl.library.rest.hooks

import spray.httpx.marshalling.{ToResponseMarshallable => Response}

/**
  * Passive REST hooks offering no extra functionality.
  * @tparam T
  */
trait PassiveRestHooks[T] extends RestHooks[T] {

  override protected def postHook(postFunction: => Response): Response = postFunction

  override protected def putHook(putFunction: => Response): Response = putFunction

  override protected def deleteHook(deleteFunction: => Response): Response = deleteFunction

  override protected def getHook(getFunction: => Response): Response = getFunction

}
