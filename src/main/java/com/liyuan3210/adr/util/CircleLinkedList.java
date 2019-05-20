package com.liyuan3210.adr.util;

import org.springframework.util.Assert;

public class CircleLinkedList<T extends CircleNode<T>> {

	// 头指针
	private T first;
	
	// 尾指针
	private T last;
	
	public CircleLinkedList() {
		
	}
	
	public CircleLinkedList(T t) {
		if (t != null) {
			this.first = this.last = t;
			this.last.next = this.first;
		}
	}
	
	/**
	 * 添加链表结点
	 */
	public void add(T t) {
		Assert.notNull(t, "add instance must not null");
		if (first == null) {
			this.first = this.last = t;
			this.last.next = this.first;
		} else {
			t.next = this.first;
			this.last.next = t;
			this.last = t;
		}
	}
	
	/**
	 * 移除链表结点
	 */
	public void remove(T t) {
		Assert.notNull(t, "remove instance must not null");
		int size = size();
		if (size == 1) {
			if (t == this.first) {
				this.first = this.last = null;
			}
		} else if (size > 1) {
			// 当前结点上一个结点
			T pre = null;
			// 当前结点
			T current = this.first;
			do {
				// 如果删除第一个结点
				if (current == t) {
					if (t == this.first) {
						this.first = this.first.next;
						this.last.next = this.first;
						
					// 如果删除最后一个结点
					} else if (t == this.last) {
						pre.next = this.first;
						this.last = pre;
					} else {
						pre.next = current.next;
					}
					break;
				}
				
				pre = current;
				current = current.next;
			} while (current != this.first);
		}
	}
	
	/**
	 * 获取循环链表长度
	 */
	public int size() {
		int i = 0;
		if (this.first == null) {
			return i;
		}
		T t = this.first;
		do {
			t = t.next;
			i++;
		} while (t != this.first);
		return i;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		T current = this.first;
		do {
			sb.append(current.toString()).append(System.getProperty("line.separator"));
			current = current.next;
		} while (current != this.first);
		return sb.toString();
	}

	public T getFirst() {
		return first;
	}
	
}
