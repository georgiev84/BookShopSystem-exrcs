package bookshopsystemapp.service;

import bookshopsystemapp.domain.entities.*;
import bookshopsystemapp.repository.AuthorRepository;
import bookshopsystemapp.repository.BookRepository;
import bookshopsystemapp.repository.CategoryRepository;
import bookshopsystemapp.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final static String BOOKS_FILE_PATH="D:\\JAVA\\HIBERNATE_SPRING_DATA\\bookshopsystem\\src\\main\\resources\\files\\books.txt";
    private final BookRepository bookRepository;
    private final FileUtil fileUtil;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, FileUtil fileUtil, AuthorRepository authorRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.fileUtil = fileUtil;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void seedBooks() throws IOException {
        // check if there is records in the DB
        if(this.bookRepository.count() !=0){
            return;
        }

        String[] booksFileContent = this.fileUtil.getFileContent(BOOKS_FILE_PATH);

        for(String line : booksFileContent){
//            String[] bookInfo = new String[5];
            String[] bookInfo = line.split("\\s+");

            Book book = new Book();
            // set author random
            book.setAuthor(this.getRandomAuthor());

            // set edition type on 0 position
            EditionType editionType = EditionType.values()[Integer.parseInt(bookInfo[0])];
            book.setEditionType(editionType);

            // parse and set date
            LocalDate releaseDate = LocalDate.parse( bookInfo[1], DateTimeFormatter.ofPattern("d/M/yyyy"));
            book.setReleaseDate(releaseDate);

            // set copies
            int copies = Integer.parseInt(bookInfo[2]);
            book.setCopies(copies);

            // set price
            BigDecimal price = new BigDecimal(bookInfo[3]);
            book.setPrice(price);

            // set age restriction
            AgeRestriction ageRestriction = AgeRestriction.values()[Integer.parseInt(bookInfo[4])];
            book.setAgeRestriction(ageRestriction);

            // set title
            StringBuilder title = new StringBuilder();
            for(int i=5; i< bookInfo.length; i++){
                title.append(bookInfo[i]).append(" ");
            }
            book.setTitle(title.toString().trim());

            // set categories
            Set<Category> categories = this.getRandomCategories();
            book.setCategories(categories);

           this.bookRepository.saveAndFlush(book);

        }

    }

    @Override
    public List<String> getAllBooksTitlesAfter() {
        List<Book> books = this.bookRepository.findAllByReleaseDateAfter(LocalDate.parse("2000-12-31"));
        return books.stream().map(book -> book.getTitle()).collect(Collectors.toList());
    }

    private Author getRandomAuthor(){
        Random random = new Random();

        int randomId = random.nextInt((int) (this.authorRepository.count()-1)) +1;

        List<Author> repository = authorRepository.findAll();

        return repository.get(randomId);
    }

    private Category getRandomCategory(){
        Random random = new Random();

        int randomId = random.nextInt((int) (this.categoryRepository.count()-1)) +1;

        List<Category> repository = categoryRepository.findAll();

        return repository.get(randomId);
    }

    private Set<Category> getRandomCategories(){
        Set<Category> categories = new LinkedHashSet<>();

        Random random = new Random();
        int lenght = random.nextInt(5);
        for (int i = 0; i < lenght; i++) {
            Category category = this.getRandomCategory();
            categories.add(category);
        }
        return categories;
    }

    @Override
    public Set<String> getAllAuthorsWithBookBefore() {
        List<Book> books = this.bookRepository.findAllByReleaseDateBefore(LocalDate.parse("1990-01-01"));
        return books.stream().map(b -> String.format("%s %s", b.getAuthor().getFirstName(), b.getAuthor().getLastName())).collect(Collectors.toSet());
    }
}
