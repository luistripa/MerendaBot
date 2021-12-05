
-- Drop all tables
drop table if exists university_event;
drop table if exists university_subject;
drop table if exists university_professor;
drop table if exists university_test;
drop table if exists university_assignment;
drop table if exists university_link;
drop table if exists university_poll;

-- Drop all types
drop type if exists event_type;
drop type if exists event_interval;

create type event_type as enum ('class', 'test', 'assignment');
create type event_interval as enum ('single', 'weekly');

create table if not exists guilds (
    id text primary key,
    name text not null,
    default_chat_id text not null
);

-- Create all tables
create table if not exists guild_event (
    id serial primary key,
    guild_id text not null references guilds(id),
    event_type event_type not null,
    event_interval event_interval not null,
    name text not null,
    start_date date not null,
    end_date date,
    start_time time not null,
    end_time time,
    link text,
    subject_id int references guild_subject(id)
);

create table if not exists guild_subject (
    id serial primary key,
    guild_id text not null references guilds(id),
    name text not null,
    short_name char(4) not null
);

create table if not exists guild_professor (
    id serial primary key,
    guild_id text not null references guilds(id),
    name text not null,
    email text unique not null,
    subject_id int references guild_subject(id)
);

create table if not exists guild_link (
    id serial primary key,
    guild_id text not null references guilds(id),
    name text not null,
    url text not null,
    subject_id int references guild_subject(id)
);

create table if not exists guild_poll (
    id serial primary key,
    guild_id text not null references guilds(id),
    message_id text not null,
    user_id text not null default 0,
    votes_for int not null default 0,
    votes_abstain int not null default 0,
    votes_against int not null default 0,
    voters text array,                      -- Holds user ids
    closed boolean not null default false

    -- Number of voters must be the same as the sum of all votes
    check ( array_length(voters, 1) == votes_for + votes_abstain + votes_against )
);

create table if not exists guild_multipoll_option (
    id serial not null,
    multipoll_id int references guild_multipoll(id),
    description text not null,
    vote_count int default 0,

    constraint pk_multipoll_option_id primary key (id, multipoll_id)
);

create table if not exists guild_multipoll (
    id serial primary key,
    guild_id text not null references guilds(id),
    message_id text not null,
    owner_id text not null
);
