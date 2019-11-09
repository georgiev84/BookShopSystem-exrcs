package bookshopsystemapp.service;

import bookshopsystemapp.domain.entities.Category;
import bookshopsystemapp.repository.CategoryRepository;
import bookshopsystemapp.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final static String CATEGORIES_FILE_PATH = "D:\\JAVA\\HIBERNATE_SPRING_DATA\\bookshopsystem\\src\\main\\resources\\files\\categories.txt";
    private final FileUtil fileUtil;
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(FileUtil fileUtil, CategoryRepository categoryRepository) {
        this.fileUtil = fileUtil;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void seedCategories() throws IOException {
        // check if there is something in the DB
        if(this.categoryRepository.count() !=0){
            return;
        }

        String[] categoryFileContent = this.fileUtil.getFileContent(CATEGORIES_FILE_PATH);

        for(String line : categoryFileContent){
            Category category = new Category();
            category.setName(line);

            this.categoryRepository.saveAndFlush(category);
        }
    }
}
