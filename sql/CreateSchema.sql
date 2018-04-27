create type platform as enum ('cross', 'android', 'ios')
;

create table users
(
	id serial not null
		constraint users_pkey
			primary key,
	email varchar(50) not null,
	password varchar(64) not null,
	salt varchar(32) not null
)
;

create unique index users_username_uindex
	on users (email)
;

create table groups
(
	id serial not null
		constraint groups_pkey
			primary key,
	name varchar(40) not null,
	hub integer
)
;

create table persons
(
	id integer not null
		constraint persons_pkey
			primary key
		constraint persons_id_fkey
			references users,
	cpr char(10) not null
		constraint persons_cpr_key
			unique,
	primarygroup integer
)
;

create table modules
(
	id serial not null
		constraint module_pkey
			primary key,
	name varchar(40) not null,
	frequencevalue integer not null,
	frequencetype varchar(40) not null,
	platform platform not null
)
;

create table hasparent
(
	child integer not null
		constraint hasparent_child_fkey
			references persons,
	parent integer not null
		constraint hasparent_parent_fkey
			references persons,
	constraint hasparent_pkey
		primary key (child, parent)
)
;

create table hasgroup
(
	person integer not null
		constraint hasgroup_person_fkey
			references persons,
	"group" integer not null
		constraint hasgroup_group_fkey
			references groups,
	constraint hasgroup_pkey
		primary key (person, "group")
)
;

create table dependent
(
	module integer not null
		constraint dependent_module_fkey
			references modules,
	dependency integer not null
		constraint dependent_dependency_fkey
			references modules,
	constraint dependent_pkey
		primary key (module, dependency)
)
;

create table hasmodule
(
	groupid integer not null
		constraint hasmodule_groupid_fkey
			references groups,
	moduleid integer not null
		constraint hasmodule_moduleid_fkey
			references modules,
	constraint hasmodule_pkey
		primary key (groupid, moduleid)
)
;

create table hubs
(
	id serial not null
		constraint hubs_pkey
			primary key
)
;

alter table groups
	add constraint groups_hub_fkey
		foreign key (hub) references hubs
;

create table researcher
(
	id integer not null
		constraint researcher_pkey
			primary key
		constraint researcher_id_fkey
			references users
				on delete cascade,
	phone integer
)
;

create table invite
(
	cpr varchar(10),
	key varchar(40)
)
;