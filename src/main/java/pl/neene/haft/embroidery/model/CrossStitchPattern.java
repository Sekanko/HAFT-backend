package pl.neene.haft.embroidery.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.neene.haft.auth.model.NeeNeUser;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "cross_stitch_pattern")
public class CrossStitchPattern {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private NeeNeUser creator;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "canvas_width", nullable = false)
    private int canvasWidth;

    @Column(name = "canvas_height", nullable = false)
    private int canvasHeight;

    @Column(name = "canvas_density", nullable = false)
    private int canvasDensity;

    @Column(name = "colors_count", nullable = false)
    private int colorCount;

    @Column(name = "pattern_key", length = 512)
    private String patternKey;

    @Column(name = "thumbnail_key", length = 512)
    private String thumbnailKey;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
