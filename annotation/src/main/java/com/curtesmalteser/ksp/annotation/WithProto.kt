package com.curtesmalteser.ksp.annotation

import com.google.protobuf.GeneratedMessageLite

/**
 * Created by António Bastião on 29.06.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@Target(AnnotationTarget.CLASS)
annotation class WithProto<T: GeneratedMessageLite<*, *>>