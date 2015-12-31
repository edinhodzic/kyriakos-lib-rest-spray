package io.otrl.library.rest.spray

import io.otrl.library.domain.Identifiable
import io.otrl.library.rest.hooks.PassiveRestHooks

/**
  * A router which provides no hooks around REST
  * functionality which it exposes for a single resource.
  * @tparam T the resource for which to expose REST functionality
  */
class DefaultSprayRestRouter[T <: Identifiable](implicit manifest: Manifest[T])
  extends SprayRestRouter[T] with PassiveRestHooks[T]
