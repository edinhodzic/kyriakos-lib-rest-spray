package io.otrl.library.rest.spray

import io.otrl.library.domain.Identifiable
import io.otrl.library.rest.hooks.KamonRestHooks

/**
  * A router which provides Kamon trace instrumentation around
  * REST functionality which it exposes for a single resource.
  * @tparam T the resource for which to expose REST functionality
  */
class KamonSprayRestRouter[T <: Identifiable](implicit manifest: Manifest[T])
  extends SprayRestRouter[T] with KamonRestHooks[T]

// TODO Kamon counters for diffeternt http responses
