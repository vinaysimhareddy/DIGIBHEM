package com.Library;

public class Member {
    int memberId;
    String name;

    public Member(int memberId, String name) {
        this.memberId = memberId;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Member ID: " + memberId + ", Name: " + name;
    }
}