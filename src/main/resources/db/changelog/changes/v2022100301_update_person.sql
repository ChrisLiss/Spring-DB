alter table if exists ulab_edu.person
add column if not exists city varchar(50);


comment on column ulab_edu.person.city is 'Город проживания пользователя';

