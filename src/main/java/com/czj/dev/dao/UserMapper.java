package com.czj.dev.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.czj.dev.domain.User;

@Mapper
public interface UserMapper {
	
	// 根据user_id查询user_inf表的记录
	@Select("select user_id as id, nickname, password, salt, head, "
			+ "register_date as registerDate, last_login_date as lastLoginDate, "
			+ "login_count as loginCount from user_inf where user_id = #{id}")
	User findById(long id);

	// 更新user_inf表的记录
	@Update("update user_inf set last_login_date = #{lastLoginDate}"
			+ ", login_count=#{loginCount} where user_id = #{id}")
	void update(User user);
	
}
