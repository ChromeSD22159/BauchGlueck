package di

import data.network.ServerHost
import org.koin.core.module.Module

val serverHost = ServerHost.LOCAL_FREDERIK.url

expect val repositoriesModule: Module