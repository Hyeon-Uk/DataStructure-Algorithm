package bst;

class BinarySearchTree<T extends Comparable<T>> {
    private Node root = null;
    private int size = 0;

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

    public int getSize() {
        return this.size;
    }

    private Node findMaxNode(Node node) {
        if (node.right == null) {
            return node;
        } else {
            return findMaxNode(node.right);
        }
    }

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
}

