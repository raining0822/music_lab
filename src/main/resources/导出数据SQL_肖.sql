/*版权曲库数据导出SQL*/

/*导出Lava的线上使用到的单曲*/
select distinct(tps.song_id),ts.song_name,ts.language,ts.duration,ts.filesize,ts.pic_id,ts.audio_url from t_program_song as tps
inner join t_song as ts on tps.song_id = ts.song_id
into outfile '/tmp/lava_song_utf8.csv'
fields terminated by '|&|'
lines terminated by '\r\n';

/*导出Lava的线上使用到的艺术家*/
select distinct(ts.artist_id),ta.zh_name,ta.en_name,ta.pic_id,ta.country,ta.sex,ta.description, ta.salbum_num, ta.song_num from t_program_song as tps
inner join t_song as ts on tps.song_id = ts.song_id 
inner join t_artist as ta on ts.artist_id = ta.artist_id
into outfile '/tmp/lava_artist_utf8.csv'
fields terminated by '|&|'
lines terminated by '\r\n';

/*导出Lava的线上使用到的专辑*/
select distinct(tsa.salbum_id),tsa.salbum_name,tsa.release_date,tsa.song_num,tsa.pic_id,tsa.company,tsa.language,tsa.area,tsa.genre,tsa.artist_id from t_program_song as tps
inner join t_song as ts on tps.song_id = ts.song_id
inner join t_salbum as tsa on ts.salbum_id = tsa.salbum_id
into outfile '/tmp/lava_salbum_utf8.csv'
fields terminated by '|&|'
lines terminated by '\r\n';

/*导出Lava的线上使用到的专辑与单曲的关系映射数据*/
select distinct(tps.song_id), tss.salbum_id,ts.artist_id  from t_program_song as tps
left join t_salbum_song as tss on tps.song_id = tss.song_id
left join t_salbum as ts on tss.salbum_id = ts.salbum_id
into outfile '/tmp/lava_song_album_utf8.csv'
fields terminated by '|&|'
lines terminated by '\r\n';