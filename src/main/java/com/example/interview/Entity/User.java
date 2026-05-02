package com.example.interview.Entity;

import com.example.interview.Enum.Career;
import com.example.interview.Enum.Gender;
import com.example.interview.Enum.Job;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

import javax.xml.stream.XMLEventWriter;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이름을 입력하세요")
    private String name;

    @Email(message = "유효한 이메일을 입력하세요")
    @NotBlank(message = "이메일을 입력하세요")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요")
    @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다")
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Job job;
    @Enumerated(EnumType.STRING)
    private Career career;
    @NotBlank(message = "전화번호를 입력하세요")
    private String phone;

    @NotNull(message = "나이를 입력해주세요.")
    @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
    private Integer age;
}

