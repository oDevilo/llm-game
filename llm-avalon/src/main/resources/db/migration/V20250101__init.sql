CREATE TABLE `fluxion_id`
(
    `id`         bigint unsigned NOT NULL AUTO_INCREMENT,
    `type`       varchar(64)     NOT NULL,
    `current_id` bigint unsigned NOT NULL DEFAULT 0,
    `step`       int             NOT NULL DEFAULT 0,
    `is_deleted` bit(1)          NOT NULL DEFAULT 0,
    `created_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_id` (`type`)
);

CREATE TABLE `fluxion_version`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT,
    `ref_id`      varchar(64)     NOT NULL,
    `ref_type`    varchar(64)     NOT NULL,
    `version`     varchar(64)     NOT NULL,
    `description` varchar(255)    NOT NULL DEFAULT '',
    `config`      MEDIUMTEXT,
    `is_deleted`  bit(1)          NOT NULL DEFAULT 0,
    `created_at`  datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_version` (`ref_id`, `ref_type`, `version`)
);

CREATE TABLE `fluxion_tag`
(
    `id`         bigint unsigned NOT NULL AUTO_INCREMENT,
    `ref_id`     varchar(64)     NOT NULL,
    `ref_type`   varchar(64)     NOT NULL,
    `tag_name`   varchar(128)    NOT NULL,
    `tag_value`  varchar(255)    NOT NULL DEFAULT '',
    `is_deleted` bit(1)          NOT NULL DEFAULT 0,
    `created_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tag` (`ref_id`, `ref_type`, `tag_name`, `tag_value`)
);

CREATE TABLE `fluxion_lock`
(
    `id`         bigint unsigned NOT NULL AUTO_INCREMENT,
    `name`       varchar(255)    NOT NULL,
    `owner`      varchar(255)    NOT NULL,
    `expire_at`  datetime(3)     NOT NULL,
    `is_deleted` bit(1)          NOT NULL DEFAULT 0,
    `created_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_lock` (`name`)
);

CREATE TABLE `fluxion_broker`
(
    `id`                bigint unsigned NOT NULL AUTO_INCREMENT,
    `host`              varchar(255)    NOT NULL,
    `port`              int                      DEFAULT 0,
    `protocol`          varchar(64)     NOT NULL,
    `broker_load`       int                      DEFAULT 0,
    `last_heartbeat_at` datetime(3)              DEFAULT NULL,
    `is_deleted`        bit(1)          NOT NULL DEFAULT 0,
    `created_at`        datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_broker` (`host`, `port`),
    KEY `idx_last_heartbeat` (`last_heartbeat_at`)
);

CREATE TABLE `fluxion_bucket`
(
    `id`         bigint unsigned NOT NULL AUTO_INCREMENT,
    `bucket`     int unsigned    NOT NULL,
    `broker_id`  varchar(64)     NOT NULL,
    `is_deleted` bit(1)          NOT NULL DEFAULT 0,
    `created_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bucket` (`bucket`),
    KEY `idx_broker` (`broker_id`)
);

CREATE TABLE `fluxion_app`
(
    `id`         bigint unsigned NOT NULL AUTO_INCREMENT,
    `app_id`     varchar(64)     NOT NULL,
    `app_name`   varchar(255)    NOT NULL,
    `is_deleted` bit(1)          NOT NULL DEFAULT 0,
    `created_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app` (`app_id`)
);

CREATE TABLE `fluxion_workflow`
(
    `id`              bigint unsigned NOT NULL AUTO_INCREMENT,
    `workflow_id`     varchar(64)     NOT NULL,
    `name`            varchar(255)    NOT NULL,
    `description`     varchar(255)    NOT NULL DEFAULT '',
    `publish_version` varchar(64)     NOT NULL DEFAULT '',
    `draft_version`   varchar(64)     NOT NULL DEFAULT '',
    `is_deleted`      bit(1)          NOT NULL DEFAULT 0,
    `created_at`      datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_workflow` (`workflow_id`)
);

CREATE TABLE `fluxion_trigger`
(
    `id`              bigint unsigned NOT NULL AUTO_INCREMENT,
    `trigger_id`      varchar(64)     NOT NULL,
    `name`            varchar(255)    NOT NULL,
    `description`     varchar(255)    NOT NULL DEFAULT '',
    `publish_version` varchar(64)     NOT NULL DEFAULT '',
    `draft_version`   varchar(64)     NOT NULL DEFAULT '',
    `is_enabled`      bit(1)          NOT NULL DEFAULT 0,
    `is_deleted`      bit(1)          NOT NULL DEFAULT 0,
    `created_at`      datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_trigger` (`trigger_id`)
);

CREATE TABLE `fluxion_execution`
(
    `id`                 bigint unsigned NOT NULL AUTO_INCREMENT,
    `execution_id`       varchar(64)     NOT NULL,
    `trigger_id`         varchar(64)     NOT NULL,
    `trigger_type`       varchar(64)     NOT NULL,
    `executable_id`      varchar(64)     NOT NULL,
    `executable_type`    varchar(64)     NOT NULL,
    `executable_version` varchar(64)     NOT NULL,
    `status`             varchar(32)     NOT NULL,
    `trigger_at`         datetime(3)              DEFAULT NULL,
    `start_at`           datetime(3)              DEFAULT NULL,
    `end_at`             datetime(3)              DEFAULT NULL,
    `is_deleted`         bit(1)          NOT NULL DEFAULT 0,
    `created_at`         datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`         datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_execution` (`execution_id`),
    UNIQUE KEY `uk_execution_trigger` (`executable_id`, `trigger_at`, `executable_type`)
);

CREATE TABLE `fluxion_schedule`
(
    `id`                 bigint unsigned NOT NULL AUTO_INCREMENT,
    `schedule_id`        varchar(64)     NOT NULL,
    `bucket`             int unsigned    NOT NULL,
    `schedule_type`      varchar(64)     NOT NULL,
    `start_time`         datetime(3)              DEFAULT NULL,
    `end_time`           datetime(3)              DEFAULT NULL,
    `schedule_delay`     bigint                   DEFAULT 0,
    `schedule_interval`  bigint                   DEFAULT NULL,
    `schedule_cron`      varchar(128)    NOT NULL DEFAULT '',
    `schedule_cron_type` varchar(32)     NOT NULL DEFAULT '',
    `last_trigger_at`    datetime(3)              DEFAULT NULL,
    `last_feedback_at`   datetime(3)              DEFAULT NULL,
    `next_trigger_at`    datetime(3)              DEFAULT NULL,
    `is_enabled`         bit(1)          NOT NULL DEFAULT 0,
    `is_deleted`         bit(1)          NOT NULL DEFAULT 0,
    `created_at`         datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`         datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_schedule` (`schedule_id`),
    KEY `idx_next_trigger_start_end_bucket` (`next_trigger_at`, `start_time`, `end_time`, `bucket`)
);

CREATE TABLE `fluxion_schedule_delay`
(
    `id`          bigint unsigned NOT NULL AUTO_INCREMENT,
    `schedule_id` varchar(64)     NOT NULL,
    `trigger_at`  datetime(3)     NOT NULL,
    `delay_id`    varchar(128)    NOT NULL,
    `bucket`      int unsigned    NOT NULL,
    `status`      varchar(32)     NOT NULL,
    `is_deleted`  bit(1)          NOT NULL DEFAULT 0,
    `created_at`  datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_schedule_delay` (`schedule_id`, `trigger_at`),
    KEY `idx_delay_trigger_bucket_status` (`delay_id`, `trigger_at`, `bucket`, `status`)
);

CREATE TABLE `fluxion_job`
(
    `id`             bigint unsigned NOT NULL AUTO_INCREMENT,
    `job_id`         varchar(64)     NOT NULL,
    `execution_id`   varchar(64)     NOT NULL,
    `bucket`         int unsigned    NOT NULL,
    `job_type`       varchar(32)     NOT NULL,
    `ref_id`         varchar(64)     NOT NULL,
    `status`         varchar(32)     NOT NULL,
    `trigger_at`     datetime(3)     NOT NULL,
    `start_at`       datetime(3)              DEFAULT NULL,
    `end_at`         datetime(3)              DEFAULT NULL,
    `worker_address` varchar(64)     NOT NULL DEFAULT '',
    `last_report_at` datetime(3)              DEFAULT NULL,
    `retry_times`    int unsigned    NOT NULL,
    `result`         TEXT,
    `error_msg`      TEXT,
    `monitor`        varchar(255)    NOT NULL DEFAULT '',
    `is_deleted`     bit(1)          NOT NULL DEFAULT 0,
    `created_at`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_job` (`job_id`),
    UNIQUE KEY `uk_job_execution` (`execution_id`, `ref_id`),
    KEY `idx_job_status_trigger` (`job_id`, `bucket`, `trigger_at`, `status`),
    KEY `idx_job_status_report` (`job_id`, `bucket`, `last_report_at`, `status`)
);

CREATE TABLE `fluxion_job_record`
(
    `id`             bigint unsigned NOT NULL AUTO_INCREMENT,
    `job_id`         varchar(64)     NOT NULL,
    `times`          int unsigned    NOT NULL,
    `start_at`       datetime(3)              DEFAULT NULL,
    `end_at`         datetime(3)              DEFAULT NULL,
    `status`         varchar(32)     NOT NULL,
    `worker_address` varchar(64)     NOT NULL DEFAULT '',
    `result`         TEXT,
    `error_msg`      TEXT,
    `is_deleted`     bit(1)          NOT NULL DEFAULT 0,
    `created_at`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_job_record` (`job_id`, `times`)
);

CREATE TABLE `fluxion_worker`
(
    `id`         bigint unsigned NOT NULL AUTO_INCREMENT,
    `worker_id`  varchar(64)     NOT NULL,
    `app_id`     varchar(64)     NOT NULL,
    `host`       varchar(255)    NOT NULL,
    `port`       int                      DEFAULT 0,
    `protocol`   varchar(64)     NOT NULL,
    `status`     varchar(32)     NOT NULL,
    `is_enabled` bit(1)          NOT NULL DEFAULT 0,
    `is_deleted` bit(1)          NOT NULL DEFAULT 0,
    `created_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_worker` (`worker_id`),
    KEY `idx_app` (`app_id`)
);

CREATE TABLE `fluxion_worker_executor`
(
    `id`         bigint unsigned NOT NULL AUTO_INCREMENT,
    `worker_id`  varchar(64)     NOT NULL,
    `name`       varchar(255)    NOT NULL,
    `is_deleted` bit(1)          NOT NULL DEFAULT 0,
    `created_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_worker_executor` (`worker_id`, `name`)
);

CREATE TABLE `fluxion_worker_metric`
(
    `id`                  bigint unsigned NOT NULL AUTO_INCREMENT,
    `worker_id`           varchar(64)     NOT NULL,
    `cpu_processors`      int             NOT NULL,
    `cpu_load`            float           NOT NULL,
    `free_memory`         bigint          NOT NULL,
    `available_queue_num` int             NOT NULL,
    `last_heartbeat_at`   datetime(3)     NOT NULL,
    `is_deleted`          bit(1)          NOT NULL DEFAULT 0,
    `created_at`          datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`          datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_worker_metric` (`worker_id`),
    KEY `idx_last_heartbeat` (`last_heartbeat_at`)
);

