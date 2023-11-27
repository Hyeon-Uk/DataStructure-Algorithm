# Binary Search Tree (BST)

---
## 이진 그래프를 이용하여 데이터를 쉽게 삽입, 삭제, 탐색 하기 위한 자료구조
```agsl
- 중복된 값이 없다고 가정한다.
- 노드의 값은 왼쪽 노드의 값보다 커야한다.
- 노드의 값은 오른쪽 노드의 값보다 작거나 같아야한다.
- 현재 노드의 값은 왼쪽 서브트리의 최댓값보다 커야한다
- 현재 노드의 값은 오른쪽 서브트리의 최솟값보다 작거나 같아야한다.
```

## 기본 구성
기본적으로 이진트리를 구성하기 위한 Node 클래스가 있으며, Root노드 및 size 변수등이 있다.<br/>
구현을 쉽게 하기 위해 parent까지 저장하도록 구현했다.

```java
class BinarySearchTree<T extends Comparable<T>> {
    private Node root = null;
    private int size = 0;
    class Node {
        T value;
        Node left, right, parent;

        public Node(T value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.parent = null;
        }

        public Node(Node parent, T value) {
            this(value);
            this.parent = parent;
        }

        public void free() {
            this.left = null;
            this.right = null;
            this.value = null;
            this.parent = null;
        }

        public void freePointers() {
            this.left = this.right = this.parent = null;
    }
}
```

## Insert
- 삽입은 해당 데이터가 들어갈 자리를 구하면된다. <br/>
- 데이터가 현재 노드보다 작다면 왼쪽 노드로, 아니라면 오른쪽 노드로 탐색하며 자리를 찾아간다.<br/>
- 해당 자리가 null이라면 거기가 데이터가 들어갈 자리이다.
- 연결해주면서 부모 노드도 같이 연결해준다.
```java
public void insert(T value) {
    Node cur = this.root;

    if (this.root == null) {//루트가 null이면 새로운 노드 넣기
        this.root = new Node(null, value);
    } else {//아니라면 넣을 자리를 찾아서 넣기
        Node parent = cur;
        boolean left = false;
        while (cur != null) {//현재 노드가 null일때까지 탐색(들어갈 위치 탐색)
            parent = cur;

            if (cur.value.compareTo(value) > 0) {
                cur = cur.left;
                left = true;
            } else {
                cur = cur.right;
                left = false;
            }
        }

        if (left) {//들어갈 자리가 부모의 왼쪽자리면 왼쪽에 노드 생성
            parent.left = new Node(parent, value);
        } else {//오른쪽이면 오른쪽에 노드 생성
            parent.right = new Node(parent, value);
        }
    }
    this.size++;//사이즈 증가
}
```

## search
- 찾으려는 수가 현재 Node의 값보다 작다면 왼쪽 Node로 이동한다.
- 찾으려는 수가 현재 Node의 값보다 크다면 오른쪽 Node로 이동한다.
- 찾으려는 수가 현재 Node의 값과 같다면, 해당 노드가 찾으려는 노드이다.
- 만약 null이라면, 해당 값은 없는것이다.
```java
public Node search(T value) {
    Node cur = this.root;

    while (cur != null) {
        if (cur.value.equals(value)) {//같으면 해당 노드에서 멈춤
            break;
        } else if (cur.value.compareTo(value) < 0) {//
            cur = cur.left;
        } else {
            cur = cur.right;
        }
    }
    return cur;
}
```

## delete
- 개인적으로 delete가 구현할것이 많았다
- 경우를 모두 따져야 한다.
1. 삭제하려는 Node의 왼쪽, 오른쪽 자식이 모두 존재하는 경우
2. 삭제하려는 Node의 왼쪽 혹은 오른쪽 자식만 살아있는 경우
3. 삭제하려는 Node의 자식이 없는 경우

```java
public void delete(T value) {
    Node target = search(value);//삭제할 노드를 탐색
    if (target == null) {//삭제할게 없으므로 return
        return;
    }

    if (target.left != null && target.right != null) {//양쪽 노드가 모두 살아있다면
        Node maxNode = findMaxNode(target.left);//왼쪽 서브트리중 가장 큰 값의 노드를 찾은 뒤 부모와의 관계를 끊음

        //max노드의 관계를 끊고, 하위 노드를 이어붙이기 위한 코드
        Node parentMaxNode = maxNode.parent;//maxNode의 부모
        Node leftMaxNode = maxNode.left;//maxNode의 왼쪽자식 ( 오른쪽 자식이 존재하지 않을때까지 내려가므로 오른쪽자식은 없다 생각해야함 )

        if (parentMaxNode == target) {//바로 왼쪽노드가 maxNode라면
            parentMaxNode.left = leftMaxNode;//부모 노드의 왼쪽자식에 maxNode의 왼쪽자식을 이어줌
        } else {
            parentMaxNode.right = leftMaxNode;//아니라면 오른쪽 자식에 maxNode의 왼쪽자식을 이어줌
        }

        if (leftMaxNode != null) {//leftMaxNode가 null이 아니라면
            leftMaxNode.parent = parentMaxNode;//leftMaxNode의 부모를 maxNode의 부모노드로 이어줌
        }

        maxNode.freePointers();

        //maxNode의 좌, 우를 target의 좌,우로 대체
        maxNode.left = target.left;
        maxNode.right = target.right;

        //target노드의 좌,우 노드의 부모를 maxNode로 대체
        target.left.parent = maxNode;
        target.right.parent = maxNode;

        if (this.root == target) {//지우려는 target이 루트라면 root 를 maxNode로 연결
            this.root = maxNode;
        } else {//root가 아니라면 부모의 연결관계 정리
            maxNode.parent = target.parent;
            if (target.parent.left == target) {//왼쪽자식이라면
                target.parent.left = maxNode;//지우려는 노드의 왼쪽자식에 maxNode를 연결
                maxNode.parent = target.parent;
            } else {
                target.parent.right = maxNode;//오른쪽 자식이라면
                maxNode.parent = target.parent;//지우려는 노드의 오른쪽 자식에 maxNode를 연결
            }
        }
    } else if (target.left != null) {//왼쪽노드만 살아있다면, 왼쪽노드의 자식을 부모와 이어주면 끝
        Node leftChild = target.left;
        if (this.root == target) {//target이 루트라면 leftChild를 루트로 설정
            leftChild.parent = null;
            this.root = leftChild;
        } else {//아니라면 지우려는 노드의 부모와 연결
            leftChild.parent = target.parent;
            target.parent.left = leftChild;
        }
    } else if (target.right != null) {//오른쪽 노드만 살아있다면, 오른쪽 노드의 자식을 부모와 이어주면 끝
        Node rightChild = target.right;
        if (this.root == target) {//target이 루트라면 rightChild를 루트로 설정
            rightChild.parent = null;
            this.root = rightChild;
        } else {//아니라면 지우려는 노드의 부모와 연결
            rightChild.parent = target.parent;
            target.parent.right = rightChild;
        }
    } else {//자식이 둘 다 없다면 걍 없애면 됨
        if (this.root == target) {//루트라면
            this.root = null;//root를 null로 처리
        } else {//아니라면
            if (target.parent.left == target) {//target이 왼쪽자식이라면
                target.parent.left = null;//부모의 왼쪽자식을 null
            } else {//오른쪽 자식이라면
                target.parent.right = null;//부모의 오른쪽 자식을 null
            }
        }
    }

    target.free();
    this.size--;
}
```

## BinarySearchTree의 한계점
- Insert 순서에 따라 균형있는 BinaryTree가 될 수 있고, 반대로 편향 그래프가 만들어질 수 있기 때문에 삽입, 삭제, 탐색의 평균 시간은 `O(logN)` 이지만, 최악의 경우 `O(N)` 이 된다. 