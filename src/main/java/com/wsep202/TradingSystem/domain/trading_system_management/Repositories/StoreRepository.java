package com.wsep202.TradingSystem.domain.trading_system_management.Repositories;

import com.wsep202.TradingSystem.domain.trading_system_management.Product;
import com.wsep202.TradingSystem.domain.trading_system_management.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
//@NoRepositoryBean
public interface StoreRepository extends JpaRepository<Store, String> {
    //UserSystem findOne(String username);

    /*@Query(value = "INSERT INTO USER_SYSTEM VALUES (:userName, :firstName,:isLogin :lastName, :password)",
            nativeQuery = true)
    default void insertUser(@Param("USER_NAME") String userName, @Param("FIRST_NAME") String firstName,
                            @Param("IS_LOGIN") Boolean isLogin, @Param("LAST_NAME") String lastName,
                            @Param("PASSWORD") String password) {
    }
*/

    List<Store> findAll();

    Store save(Store store);
    Store findByStoreId(int storeId);

}