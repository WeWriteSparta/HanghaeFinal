package com.example.hanghaefinal.model;


import com.example.hanghaefinal.dto.requestDto.BookmarkRequestDto;
import com.example.hanghaefinal.dto.requestDto.PostLikesRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "book_mark")
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;


    public Bookmark(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    public Bookmark(BookmarkRequestDto bookmarkRequestDto) {

        this.user = bookmarkRequestDto.getUser();
        this.post = bookmarkRequestDto.getPost();
    }
}