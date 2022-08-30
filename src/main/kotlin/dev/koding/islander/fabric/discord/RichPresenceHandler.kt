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

package dev.koding.islander.fabric.discord

import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.IPCListener
import com.jagrosh.discordipc.entities.DiscordBuild
import com.jagrosh.discordipc.entities.RichPresence
import com.jagrosh.discordipc.exceptions.NoDiscordClientException
import dev.koding.islander.fabric.IslanderFabric
import lombok.Getter
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ServerData
import java.time.OffsetDateTime
import java.util.*

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

class RichPresenceHandler : IPCListener {
    private var timer: Timer? = null
    override fun onReady(client: IPCClient) {
        timestamp = OffsetDateTime.now()
        richPresence(client)
        timer = Timer()
        runTimer(client)
    }

    fun stopTimer() {
        timer!!.cancel()
    }

    /**
     * This runs the timer for the rich presence task.
     *
     * @param client The client will be the MCC Island discord app
     */
    private fun runTimer(client: IPCClient) {
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                IslanderFabric.logger.info("Rich presence timertask run")
                try {
                    client.connect(DiscordBuild.ANY)
                    richPresence(client)
                } catch (ignored: NoDiscordClientException) {
                }
            }
        }, 0, 3000)
    }

    companion object {
        @Getter
        private var timestamp: OffsetDateTime? = null

        /**
         * This will create a new, updated rich presence each time the TimerTask is run.
         * For now, I'm just setting this to In-Game until I find a good way to check
         * what game server a player is on.
         *
         * @param client The client will be the MCC Island discord app
         */
        fun richPresence(client: IPCClient) {
            val b = RichPresence.Builder()
                .setStartTimestamp(timestamp)
            val minecraft = Minecraft.getInstance()

            val serverEntry: ServerData? = minecraft.currentServer
            // Check if the game is on a server before doing anything
            if (serverEntry != null) {
                IslanderFabric.logger.info("Current server entry did not match MCC Island: " + serverEntry.ip)
                if (serverEntry.ip.contains("mccisland.net")) {
                    b.setState("In-Game")
                    try {
                        client.sendRichPresence(b.build())
                        IslanderFabric.logger.info("Updated Discord Rich Presence")
                    } catch (e: IllegalStateException) {
                        IslanderFabric.logger.warn("No Discord, skipping Rich Presence")
                    }
                }
            } else {
                try {
                    client.sendRichPresence(null)
                    IslanderFabric.logger.info("Cleared presence")
                } catch (e: IllegalStateException) {
                    IslanderFabric.logger.warn("No Discord, skipping Rich Presence")
                }
            }
        }
    }
}