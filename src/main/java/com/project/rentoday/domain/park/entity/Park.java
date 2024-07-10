    package com.project.rentoday.domain.park.entity;

    import com.project.rentoday.domain.member.entity.Member;
    import com.project.rentoday.global.entity.BaseEntity;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Positive;
    import lombok.*;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Getter
    @NoArgsConstructor
    @Table(name = "park")
    public class Park extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "pa_id", nullable = false)
        private Long id;

        @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id")
        private Member member;

        @OneToMany(mappedBy = "park", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<ParkImage> parkImages = new ArrayList<>();

        @NotNull
        @Column
        private String parkingNum;

        @NotNull
        @Column
        private String carNum;

        @Column
        private String latitude;

        @Column
        private String longitude;

        @NotNull
        @Column
        private String confirmation;

        @Column(name = "is_signed")
        private ParkStatus parkStatus;

        @NotNull
        @Column
        private String address;

        @NotNull
        @Positive
        @Column
        private double price;

        @Column
        private String agency;

        @Column(name = "ag_num")
        private String agNum;

        @NotNull
        @Column(name = "start_time")
        private LocalDateTime startTime;

        @NotNull
        @Column(name = "end_time")
        private LocalDateTime endTime;

        @Column
        private String content;

        @Builder
        public Park(Member member, String carNum, String parkingNum,
                    String latitude, String longitude,
                    String address, String agency, String agNum,
                    LocalDateTime startTime, LocalDateTime endTime, double price, String content,
                    String confirmation) {
            this.member = member;
            this.carNum = carNum;
            this.parkingNum = parkingNum;
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
            this.agency = agency;
            this.agNum = agNum;
            this.startTime = startTime;
            this.endTime = endTime;
            this.price = price;
            this.content = content;
            this.confirmation = confirmation;
            this.parkStatus = ParkStatus.WAIT; //대기 상태
        }

        public void addParkImages(ParkImage parkImage) {
            parkImages.add(parkImage);
            parkImage.setPark(this);
        }

        public void updateParkStatus(ParkStatus parkStatus) {
            this.parkStatus = ParkStatus.CONFIRMED;
        }

        public void updateParkInfo(LocalDateTime startTime, LocalDateTime endTime, double price, String content) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.price = price;
            this.content = content;
        }

        public void setConfirmation(String confirmation) {
            this.confirmation = confirmation;
        }

        public void setRejected() {
            this.parkStatus = ParkStatus.REJECTED;
        }

        public void setConfirmed() {
            this.parkStatus = ParkStatus.CONFIRMED;
        }
    }
