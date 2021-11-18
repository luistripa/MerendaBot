
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

-- Create all tables
create table if not exists university_event (
    id serial primary key,
    event_type event_type not null,
    event_interval event_interval not null,
    name text not null,
    start_date date not null,
    end_date date,
    start_time time not null,
    end_time time,
    link text,
    subject_id int
);

create table if not exists university_subject (
    id serial primary key,
    name text not null,
    short_name char(4) not null
);

create table if not exists university_professor (
    id serial primary key,
    name text not null,
    email text unique not null,
    subject_id int
);

create table if not exists university_test (
    id serial primary key,
    name text not null,
    datetime timestamp not null,
    subject_id int not null
);

create table if not exists university_assignment (
    id serial primary key,
    name text not null,
    datetime timestamp not null,
    subject_id int not null
);

create table if not exists university_link (
    id serial primary key,
    name text not null,
    url text not null,
    subject_id int
);

create table if not exists university_poll (
    id serial primary key,
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

-- Foreign keys
alter table university_event add constraint fk_event_subject foreign key (subject_id) references university_subject(id);
alter table university_professor add constraint fk_professor_subject foreign key (subject_id) references university_subject(id);
alter table university_link add constraint fk_link_subject foreign key (subject_id) references university_subject(id);
alter table university_assignment add constraint fk_assignment_subject foreign key (subject_id) references university_subject(id);
