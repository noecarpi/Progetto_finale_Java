package it.aulab.progetto_finale_java.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter  
@NoArgsConstructor 
@Entity 
@Table (name="articles")
public class Article {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, length = 100)
    @NotEmpty 
    @Size (max = 100)
    private String title;

    @Column (nullable = false, length = 100)
    @NotEmpty 
    @Size (max = 100)
    private String subtitle;

    @Column (nullable = false, length = 10000)
    @NotEmpty 
    @Size (max = 10000)
    private String body;

    // @Column (nullable = true, length = 8)
    // @NotNull 
    // private LocalDate publishDate;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime publishDate = LocalDateTime.now();

    @Column (nullable = true)
    private Boolean isAccepted;


    @ManyToOne 
    @JoinColumn (name = "user_id")
    @JsonIgnoreProperties ({"articles"})
    private User user;

    @ManyToOne 
    @JsonIgnoreProperties ({"articles"})
    private Category category;

    @OneToOne (mappedBy = "article")
    @JsonIgnoreProperties ({"article"})
    private Image image;

    @Override 
    public boolean equals(Object obj){
        Article article = (Article) obj;

        if (title.equals(article.getTitle()) &&
            subtitle.equals(article.getSubtitle()) &&
            body.equals(article.getBody()) &&
            category.getName().equals(article.getCategory().getName()) &&
            java.util.Objects.equals(
                image == null ? null : image.getPath(),
                article.getImage() == null ? null : article.getImage().getPath())) {
            return true;
        }
        return false;
    }
}
