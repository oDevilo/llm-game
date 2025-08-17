/*
 * Copyright 2025-2030 limbo-io Team (https://github.com/limbo-io).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.devil.llm.avalon.controller;

import io.github.devil.llm.avalon.game.GameState;
import io.github.devil.llm.avalon.game.service.GameService;
import io.github.devil.llm.avalon.utils.json.JacksonUtils;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.RunnableConfig;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Devil
 */
@RestController
public class GameController {

    @Resource
    private GameService gameService;

    @RequestMapping("/api/v1/game/new")
    public void newGame() {
        GameState.Game game = gameService.create(5);
        RunnableConfig config = RunnableConfig.builder()
            .threadId(game.getId())
            .streamMode(CompiledGraph.StreamMode.SNAPSHOTS)
            .build();
        System.out.println(JacksonUtils.toJSONString(gameService.invoke(
            game, config
        )));
    }

    @RequestMapping("/api/v1/game/continue")
    public void continueGame(String id) {
        GameState.Game game = gameService.get(id);
        RunnableConfig config = RunnableConfig.builder()
            .threadId(game.getId())
            .streamMode(CompiledGraph.StreamMode.SNAPSHOTS)
            .build();
        System.out.println(JacksonUtils.toJSONString(gameService.invoke(
            game, config
        )));
    }

}
