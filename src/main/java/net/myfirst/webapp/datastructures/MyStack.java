//package net.myfirst.webapp.datastructures;
//
//import java.util.Stack;
//
//public class MyStack {
//    Node top;
//
//    public void push(int i) {
//        Node newNode = new Node();
//        newNode.setValue(i);
//
//        if (top == null) {
//            top = newNode;
//        } else {
//            newNode.setNext(top);
//            top = newNode;
//        }
//    }
//
//    public int pop() {
//        if (top == null) {
//            return 0;
//        }
//        Node currentNode = top;
//
//        return currentNode.getValue();
//    }
//}
