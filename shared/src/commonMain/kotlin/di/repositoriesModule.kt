package di

import data.network.ServerHost
import org.koin.core.module.Module

val serverHost = ServerHost.LOCAL_SABINA.url

expect val repositoriesModule: Module