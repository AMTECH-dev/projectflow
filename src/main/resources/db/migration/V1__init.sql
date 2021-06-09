create schema if not exists pf;

create type user_position as enum(
    'PROJECT_LEAD',
    'DIRECTION_LEAD',
    'DIRECTOR'
);

create type project_status as enum(
    'UNAPPROVED',
    'ON_PL_PLANNING',
    'ON_DL_APPROVING',
    'ON_DIRECTOR_APPROVING',
    'DIRECTOR_APPROVED',
    'DONE'
);

create table pf.employee(
    id serial primary key,
    name varchar(255) not null,
    email varchar(50) check (email ~ $$^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$$) not null,
    phone varchar(50),
    position user_position not null,
    is_fired boolean not null default false
);

create table pf.auth_user (
    employee_id bigint primary key,
    login varchar(50) not null,
    password varchar(255) not null,
    is_locked boolean not null default false,
    constraint auth_user_has_employee_fk foreign key (employee_id) references pf.employee (id) on delete cascade on update cascade
);

create unique index auth_user_login on pf.auth_user (lower(login));

create table pf.direction
(
    id serial primary key,
    lead_id bigint not null,
    name varchar(255) not null,
    constraint direction_has_lead_fk foreign key(lead_id) references pf.employee(id) on delete restrict on update cascade
);

-- Add trigger for position

create table pf.project
(
    id serial primary key,
    name varchar(255) not null,
    project_lead_id bigint not null,
    direction_id bigint not null,
    description varchar(2048),
    create_date timestamptz not null,
    status project_status not null,

    constraint project_has_project_lead_fk foreign key(project_lead_id) references pf.employee(id) on delete restrict on update cascade,
    constraint project_has_direction_fk foreign key(direction_id) references pf.direction(id) on delete restrict on update cascade
);

create table pf.project_comment(
    id serial primary key,
    project_id bigint not null,
    message varchar(5000) not null,
    create_date timestamptz not null default now(),
    login varchar(50) not null,
    constraint project_comment_has_project_fk foreign key(project_id) references pf.project(id) on delete cascade on update cascade
);

create table pf.milestone
(
    id serial primary key,
    project_id bigint not null,
    name varchar(255) not null,
    description varchar(2048),
    planned_start_date timestamptz not null,
    planned_finish_date timestamptz not null,
    fact_start_date timestamptz,
    fact_finish_date timestamptz,
    progress_percent smallint not null default 0 check (progress_percent >= 0 AND progress_percent <= 100),
    constraint milestone_has_project_fk foreign key(project_id) references pf.project(id) on delete cascade on update cascade
);

create table pf.project_journal
(
    id serial primary key,
    project_id bigint not null,
    login varchar(50) not null,
    update_date timestamptz not null,
    current_state jsonb not null,

    constraint project_journal_has_project_fk foreign key(project_id) references pf.project(id) on delete cascade on update cascade
);

create table pf.notification
(
    id serial primary key,
    recepient varchar(50) not null check (recepient ~ $$^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$$),
    sender varchar(50) not null check (sender ~ $$^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$$),
    subject varchar(255),
    body varchar,
    create_date timestamptz
);
