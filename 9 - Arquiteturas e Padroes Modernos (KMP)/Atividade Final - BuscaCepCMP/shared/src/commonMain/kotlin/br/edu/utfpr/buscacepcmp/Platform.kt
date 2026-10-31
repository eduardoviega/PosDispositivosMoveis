package br.edu.utfpr.buscacepcmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform