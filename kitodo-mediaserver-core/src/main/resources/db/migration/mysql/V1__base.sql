create table action_parameter (
  action_data_id integer not null,
  parameter varchar(255),
  parameter_key varchar(255) not null,
  primary key (action_data_id, parameter_key)
) engine=InnoDB default charset=utf8;

create table action_data (
  id integer not null auto_increment,
  action_name varchar(255),
  end_time datetime(6),
  request_time datetime(6),
  start_time datetime(6),
  work_id varchar(255) not null,
  primary key (id)
) engine=InnoDB default charset=utf8;

create table collection (
  name varchar(255) not null,
  primary key (name)
) engine=InnoDB default charset=utf8;

create table user (
  username varchar(255) not null,
  enabled bit,
  name varchar(255),
  password varchar(60),
  primary key (username)
) engine=InnoDB default charset=utf8;

create table work (
  id varchar(255) not null,
  host_id varchar(255),
  index_time datetime(6),
  path varchar(255),
  title varchar(255),
  allowed_network varchar(255),
  primary key (id)
) engine=InnoDB default charset=utf8;

create table work_collection (
  work_id varchar(255) not null,
  collection_name varchar(255) not null,
  primary key (work_id, collection_name)
) engine=InnoDB default charset=utf8;

alter table action_parameter
  add constraint FK_action_parameter_action_data_id
  foreign key (action_data_id)
  references action_data (id);

alter table action_data
  add constraint FK_action_data_work_id
  foreign key (work_id)
  references work (id);

alter table work_collection
  add constraint FK_work_collection_collection_name
  foreign key (collection_name)
  references collection (name);

alter table work_collection
  add constraint FK_work_collection_work_id
  foreign key (work_id)
  references work (id);

insert into `user` (`username`, `enabled`, `name`, `password`) values
('admin', b'1', 'Administrator', '$2a$10$oxfH7pRjIZcyB97LJ7/eSeSBEQyUE65kBiOZYADJ4wFYPkD2YqMiG');
