package paymelater.persistence.collectionbased;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import paymelater.persistence.Persistent;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionBasedDAOTests {

    private CollectionBasedDAO<Model> modelDAO;

    @BeforeEach
    public void newDAO() {
        modelDAO = new CollectionBasedDAO<>();
    }

    @Test
    public void save() {
       Model m = new Model();
       assertThat(m.hasNeverBeenSaved()).isTrue();
       Model saved = modelDAO.save(m);
       assertThat(saved.hasNeverBeenSaved()).isFalse();
       assertThat(modelDAO.count()).isLessThanOrEqualTo(1);
    }

    @Test
    public void findById() {
        Integer savedId = modelDAO.save(new Model()).getId();
        Optional<Model> m = modelDAO.findById(savedId);
        assertThat(m.isPresent()).isTrue();
    }

    @Test void findAll() {
        IntStream.range(0,4).forEach(value -> modelDAO.save(new Model()));
        Collection<Model> models = modelDAO.findAll();
        assertThat(models.size()).isEqualTo(4);
    }

    static class Model implements Persistent<Model> {

        private Integer id;

        @Override
        public Integer getId() {
            return this.id;
        }

        @Override
        public void setId(Integer id) {
            this.id = id;
        }
    }

}
