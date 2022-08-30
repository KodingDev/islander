/*
 * Copyright (c) 2022.
 * Islander by Koding Dev <hello@koding.dev>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.koding.islander.fabric

import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.entities.DiscordBuild
import com.jagrosh.discordipc.exceptions.NoDiscordClientException
import dev.koding.islander.fabric.discord.RichPresenceHandler
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

class IslanderFabric : ModInitializer {
    var logger: org.slf4j.Logger = LoggerFactory.getLogger("islander")
    private var client: IPCClient? = null
    override fun onInitialize() {
        logger = LoggerFactory.getLogger("islander")
        connect()
    }

    private fun connect() {
        client = IPCClient(1013975181833809970L)
        client!!.setListener(RichPresenceHandler())
        try {
            client!!.connect(DiscordBuild.ANY)
            logger.info("Rich presence enabled.")
        } catch (e: NoDiscordClientException) {
            logger.info("No Discord client found. Skipping rich presence.")
        } catch (ignore: IllegalStateException) {
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    companion object {
        val logger: org.slf4j.Logger = LoggerFactory.getLogger("islander")
    }
}