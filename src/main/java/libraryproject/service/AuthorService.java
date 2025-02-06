package libraryproject.service;

import libraryproject.entity.Author;
import libraryproject.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Author getAuthorById(Long id) {
        return authorRepository.findById(id).orElse(null);
    }

    public void deleteAuthorById(Long id) {
        authorRepository.deleteById(id);
    }

    public List<Author> findAuthorsByIds(List<Long> ids) {
        return authorRepository.findAllById(ids);
    }
}
