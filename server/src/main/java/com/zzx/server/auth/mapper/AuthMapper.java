package com.zzx.server.auth.mapper;

import com.zzx.server.auth.model.LoginUserView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthMapper {

    @Select("""
            SELECT u.id AS userId,
                   u.username,
                   u.password,
                   u.nickname,
                   r.role_code AS roleCode,
                   s.id AS shopId
            FROM tm_user u
            JOIN tm_user_role ur ON ur.user_id = u.id AND ur.is_deleted = 0
            JOIN tm_role r ON r.id = ur.role_id AND r.is_deleted = 0
            LEFT JOIN tm_shop s ON s.owner_user_id = u.id AND s.is_deleted = 0
            WHERE u.username = #{username}
              AND u.is_deleted = 0
              AND u.status = 'ACTIVE'
            LIMIT 1
            """)
    LoginUserView findByUsername(String username);
}