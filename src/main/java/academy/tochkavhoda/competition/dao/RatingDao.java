package academy.tochkavhoda.competition.dao;

import academy.tochkavhoda.competition.model.Application;
import academy.tochkavhoda.competition.model.Expert;

public interface RatingDao {
    void setRating(Application application, Expert expert, int rating);

    boolean delete(Application application, Expert expert);
}
