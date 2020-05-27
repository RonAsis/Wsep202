package com.wsep202.TradingSystem.domain.trading_system_management;

import com.wsep202.TradingSystem.domain.image.ImagePath;
import com.wsep202.TradingSystem.domain.image.ImageUtil;
import com.wsep202.TradingSystem.domain.trading_system_management.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingSystemDataBaseDao implements TradingSystemDao{

    //private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    //private final AdminRepository administrators;

    @Override
    public void registerAdmin(UserSystem admin) {

    }

    private boolean isNull(Object object){
        if(object == null)
            return false;
        return true;
    }

    @Override
    public boolean isRegistered(UserSystem userSystem) {
        // if NULL returned then the user isn't register
        return isNull(userRepository.findByUserName(userSystem.getUserName()));
    }

    @Override
    public void addUserSystem(UserSystem userToRegister, MultipartFile image) {
        /*userRepository.insertUser(userToRegister.getUserName(),userToRegister.getFirstName(),false, userToRegister.getLastName(),
        userToRegister.getPassword());*/

        String urlImage = null;
        if (Objects.nonNull(image)) {
            urlImage = ImageUtil.saveImage(ImagePath.ROOT_IMAGE_DIC + ImagePath.USER_IMAGE_DIC + image.getOriginalFilename(), image);
        }
        userToRegister.setImageUrl(urlImage);
        userRepository.save(userToRegister);
    }

    @Override
    public Optional<UserSystem> getUserSystem(String username) {
        return Optional.ofNullable(userRepository.findByUserName(username));
    }

    @Override
    public boolean isAdmin(String username) {
        return userRepository.findByUserName(username).isAdmin();
    }

    @Override
    public Optional<UserSystem> getAdministratorUser(String username) {
        Optional<UserSystem> userSystemOptional = Optional.ofNullable(userRepository.findByUserName(username));
        if(userSystemOptional != null)
            if(userSystemOptional.get().isAdmin())
                return userSystemOptional;
        return Optional.empty();
    }

    @Override
    public Optional<Store> getStore(int storeId) {
        return Optional.empty();
    }

    @Override
    public List<Product> searchProductByName(String productName) {
        return null;
    }

    @Override
    public List<Product> searchProductByCategory(ProductCategory productCategory) {
        return null;
    }

    @Override
    public List<Product> searchProductByKeyWords(List<String> keyWords) {
        return null;
    }

    @Override
    public void addStore(Store newStore) {

    }

    @Override
    public Set<Store> getStores() {
        return null;
    }

    @Override
    public Set<Product> getProducts() {
        return null;
    }

    @Override
    public Set<UserSystem> getUsers() {
        return null;
    }
}
