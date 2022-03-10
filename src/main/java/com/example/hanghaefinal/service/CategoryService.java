package com.example.hanghaefinal.service;

import com.example.hanghaefinal.dto.requestDto.CategoryRequestDto;
import com.example.hanghaefinal.dto.responseDto.CategoryResponseDto;
import com.example.hanghaefinal.dto.responseDto.CommentResponseDto;
import com.example.hanghaefinal.dto.responseDto.ParagraphResDto;
import com.example.hanghaefinal.dto.responseDto.PostResponseDto;
import com.example.hanghaefinal.model.*;
import com.example.hanghaefinal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PostLikesRepository postLikesRepository;
    private final ParagraphRepository paragraphRepository;
    private final CommentRepository commentRepository;
    private final CommentLikesRepository commentLikesRepository;

    public List<CategoryResponseDto> showCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponseDto> categoryResponseDtoList = new ArrayList<>();
        for (Category category:categories){
            CategoryResponseDto categoryResponseDto = new CategoryResponseDto(category);
            categoryResponseDtoList.add(categoryResponseDto);
        }
        return categoryResponseDtoList;

    }

    public List<PostResponseDto> showCategoryPosts(CategoryRequestDto categoryRequestDto) {
        String categoryName = categoryRequestDto.getCategory();

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        List<Post> posts = new ArrayList<>();
        List<Category> categories = categoryRepository.findByCategoryNameOrderByModifiedAtDesc(categoryName);

        for (Category category: categories){
            Post post = category.getPost();
            posts.add(post);
        }


        int postLikeCnt;
        for (Post post: posts ) {
            List<PostLikes> postLikesList = postLikesRepository.findAllByPostId(post.getId());
            postLikeCnt = postLikesList.size();

            List<Paragraph> paragraphList = paragraphRepository.findAllByPostIdOrderByModifiedAtDesc(post.getId());
            List<ParagraphResDto> paragraphResDtoList = new ArrayList<>();

            for(Paragraph paragraph: paragraphList){
                paragraphResDtoList.add(new ParagraphResDto(paragraph));
            }

            List<Comment> commentList = commentRepository.findAllByPostIdOrderByModifiedAtDesc(post.getId());
            List<CommentResponseDto> commentResDtoList = new ArrayList<>();

            // List<Comment>를 각각 List<CommentResponseDto> 에 담는다
            for (Comment comment:commentList ) {
                Long commentLikesCnt = commentLikesRepository.countByComment(comment);
                commentResDtoList.add(new CommentResponseDto(comment, commentLikesCnt));
            }

            List<Category> categoryList = categoryRepository.findAllByPostIdOrderByModifiedAtDesc(post.getId());
            List<CategoryResponseDto> categoryResDtoList = new ArrayList<>();

            // List<Category>에 있는 정보를 List<CategoryResponseDto> 에 담는다.
            for(Category category: categoryList){
                categoryResDtoList.add(new CategoryResponseDto(category));
            }

            String postUsername = null;
            if (post.getUser() != null) {
                postUsername = post.getUser().getUsername();
            }

            //PostResponseDto postResponseDto = new PostResponseDto(post);
            PostResponseDto postResponseDto = new PostResponseDto(post, paragraphResDtoList, commentResDtoList, categoryResDtoList, postLikeCnt, postUsername);
            postResponseDtoList.add(postResponseDto);
        }
        return postResponseDtoList;
    }
}
