/*
 * Copyright 2025-2030 Fluxion Team (https://github.com/Fluxion-io).
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

package io.github.devil.llm.avalon.dao.repository;

import io.github.devil.llm.avalon.dao.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author Devil
 * @since 2022/7/18
 */
public interface MessageEntityRepository extends JpaRepository<MessageEntity, Long>, JpaSpecificationExecutor<MessageEntity> {

    List<MessageEntity> findByGameId(String gameId);

    List<MessageEntity> findByGameIdAndRoundAndTurn(String gameId, Integer round, Integer turn);
}
