-- Adding Occupied Palestinian territory

insert into fra_country(id,iso3,countryarea,landarea,name,ctype) values (99900003, 'PSE', 602, 602, 'Occupied Palestinian territory', 'FRA');
insert into fra_status (id, status) values (9996, 'empty');
insert into fra_survey (id , country_id,status_id) values (9996, 99900003, 9996);


