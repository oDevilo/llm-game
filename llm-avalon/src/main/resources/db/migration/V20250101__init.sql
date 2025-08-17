CREATE TABLE `llm_avalon_game`
(
    `id`            bigint unsigned NOT NULL AUTO_INCREMENT,
    `game_id`       varchar(64)     NOT NULL,
    `player_number` int             NOT NULL DEFAULT 0,
    `player_roles`  TEXT,
    `captain_order` TEXT,
    `mission_camp`  varchar(64)     NOT NULL,
    `state`         varchar(64)     NOT NULL,
    `is_deleted`    bit(1)          NOT NULL DEFAULT 0,
    `created_at`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_game` (`game_id`)
);

CREATE TABLE `llm_avalon_round`
(
    `id`         bigint unsigned NOT NULL AUTO_INCREMENT,
    `game_id`    varchar(64)     NOT NULL,
    `round`      int             NOT NULL DEFAULT 0,
    `state`      varchar(64)     NOT NULL,
    `is_deleted` bit(1)          NOT NULL DEFAULT 0,
    `created_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_game_round` (`game_id`, `round`)
);

CREATE TABLE `llm_avalon_turn`
(
    `id`             bigint unsigned NOT NULL AUTO_INCREMENT,
    `game_id`        varchar(64)     NOT NULL,
    `round`          int             NOT NULL DEFAULT 0,
    `turn`           int             NOT NULL DEFAULT 0,
    `captain_number` int             NOT NULL DEFAULT 0,
    `un_speakers`    TEXT,
    `team`           TEXT,
    `vote_result`    TEXT,
    `mission_result` TEXT,
    `state`          varchar(64)     NOT NULL,
    `is_deleted`     bit(1)          NOT NULL DEFAULT 0,
    `created_at`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_game_round_turn` (`game_id`, `round`, `turn`)
);

CREATE TABLE `llm_avalon_message`
(
    `id`         bigint unsigned NOT NULL AUTO_INCREMENT,
    `game_id`    varchar(64)     NOT NULL,
    `type`       varchar(64)     NOT NULL,
    `source`     varchar(64)     NOT NULL,
    `data`       TEXT,
    `is_deleted` bit(1)          NOT NULL DEFAULT 0,
    `created_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_game` (`game_id`)
);

CREATE TABLE `llm_avalon_checkpoint`
(
    `id`            bigint unsigned NOT NULL AUTO_INCREMENT,
    `checkpoint_id` varchar(64)     NOT NULL,
    `thread_id`     varchar(64)     NOT NULL,
    `state`         TEXT,
    `node_id`       varchar(64)     NOT NULL,
    `next_node_id`  varchar(64)     NOT NULL,
    `is_deleted`    bit(1)          NOT NULL DEFAULT 0,
    `created_at`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_checkpoint` (`checkpoint_id`),
    KEY `idx_thread` (`thread_id`)
);

