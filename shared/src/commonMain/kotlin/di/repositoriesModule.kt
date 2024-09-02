package di

import de.frederikkohler.bauchglueck.shared.BuildKonfig
import org.koin.core.module.Module

val serverHost = BuildKonfig.API_HOST

expect val repositoriesModule: Module