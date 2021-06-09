insert into pf.employee(id, name, email, phone, position, is_fired)
values (1, 'ОЛЕГ', 'my-email@gmail.com', '999999999', 'DIRECTOR', false),
       (2, 'ВАНА', 'my-email@gmail.com', '999999999', 'DIRECTION_LEAD', false),
       (4, 'ФЕДОР', 'my-email@gmail.com', '999999999', 'PROJECT_LEAD', false),
       (8, 'Виктор', 'vik-email@gmail.com', '999997779', 'PROJECT_LEAD', false),
       (10, 'Петя', 'emailemail@gmail.com', '89649202323', 'DIRECTION_LEAD', false);

insert into pf.auth_user(employee_id, login, password)
values (1, 'gmailgmail', '{bcrypt}hash_super_pass'),
       (2, 'super@gmail.com', '{bcrypt}hash_super_pass'),
       (4, 'my-email@gmail.com', '{bcrypt}hash_super_pass');
