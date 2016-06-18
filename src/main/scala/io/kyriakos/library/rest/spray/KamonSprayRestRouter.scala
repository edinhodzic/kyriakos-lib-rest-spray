package io.kyriakos.library.rest.spray

import io.kyriakos.library.domain.Identifiable
import io.kyriakos.library.rest.hooks.KamonRestHooks

/**
  * A router which provides Kamon trace instrumentation around
  * REST functionality which it exposes for a single resource.
  * @tparam T the resource for which to expose REST functionality
  */
class KamonSprayRestRouter[T <: Identifiable](implicit manifest: Manifest[T])
  extends SprayRestRouter[T] with KamonRestHooks[T]

// TODO Kamon counters for diffeternt http responses
