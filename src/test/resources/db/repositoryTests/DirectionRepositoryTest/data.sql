insert into pf.employee(id, name, email, phone, position, is_fired)
values (1, 'ОЛЕГ', 'my-email@gmail.com', '999999999', 'DIRECTOR', false),
       (2, 'ВАНА', 'my-email@gmail.com', '999999999', 'DIRECTION_LEAD', false),
       (4, 'ФЕДОР', 'my-email@gmail.com', '999999999', 'PROJECT_LEAD', false),
       (10, 'Петя', 'emailemail@gmail.com', '89649202323', 'DIRECTION_LEAD', false);

insert into pf.direction(id, lead_id, name)
values (2, 2, 'dev'),
       (10, 10, 'test');

