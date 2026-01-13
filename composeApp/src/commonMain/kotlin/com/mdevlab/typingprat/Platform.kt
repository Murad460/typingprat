package com.mdevlab.typingprat

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform